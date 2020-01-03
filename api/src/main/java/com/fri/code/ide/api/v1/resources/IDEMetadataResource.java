package com.fri.code.ide.api.v1.resources;

import com.fri.code.ide.api.v1.dtos.ApiError;
import com.fri.code.ide.lib.HistoryMetadata;
import com.fri.code.ide.lib.IDEMetadata;
import com.fri.code.ide.services.beans.HistoryMetadataBean;
import com.fri.code.ide.services.beans.IDEMetadataBean;
import org.eclipse.microprofile.metrics.annotation.Timed;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.TreeSet;

@ApplicationScoped
@Path("/script")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class IDEMetadataResource {
    @Inject
    private IDEMetadataBean ideMetadataBean;
    @Inject
    private HistoryMetadataBean historyMetadataBean;


    @GET
    @Timed
    public Response getAllScripts() {
        List<IDEMetadata> scripts = ideMetadataBean.getAllScripts();
        return Response.ok(scripts).build();
    }

    @GET
    @Path("{exerciseID}")
    public Response getScriptsForExercise(@PathParam("exerciseID") Integer exerciseID) {
        IDEMetadata script = ideMetadataBean.getIDEForExercise(exerciseID);
        List<HistoryMetadata> historyMetadata = historyMetadataBean.getHistoryForExercise(exerciseID);
        script.setCodeHistory(historyMetadata);
        return Response.ok(script).build();
    }

    @PATCH
    @Path("{scriptID}")
    public Response submitScript(@PathParam("scriptID") Integer scriptID, IDEMetadata ideMetadata) {
        ideMetadata = ideMetadataBean.updateIDEMetadata(scriptID, ideMetadata);
        if (ideMetadata == null) {
            return Response.status(Response.Status.NOT_FOUND).entity(getNotFoundApiError("")).build();
        }
        historyMetadataBean.createHistoryMetadata(ideMetadata);
        List<HistoryMetadata> historyMetadata = historyMetadataBean.getHistoryForExercise(ideMetadata.getExerciseID());
        ideMetadata.setCodeHistory(historyMetadata);
        return Response.status(Response.Status.OK).entity(ideMetadata).build();
    }

    @POST
    public Response createInput(IDEMetadata ideMetadata) {
        ApiError error = new ApiError();
        error.setCode(Response.Status.BAD_REQUEST.toString());
        error.setMessage("You are missing some of the parameters");
        error.setStatus(Response.Status.BAD_REQUEST.getStatusCode());
        if (ideMetadata.getCode() == null || ideMetadata.getExerciseID() == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        }
        else{
            try {
                ideMetadata = ideMetadataBean.createIDEMetadata(ideMetadata);
                historyMetadataBean.createHistoryMetadata(ideMetadata);
                List<HistoryMetadata> historyMetadata = historyMetadataBean.getHistoryForExercise(ideMetadata.getExerciseID());
                ideMetadata.setCodeHistory(historyMetadata);
            } catch (Exception e) {
                error.setMessage(e.getMessage());
                return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
            }
        }
        return Response.ok(ideMetadata).build();
    }

    @PATCH
    @Path("revert")
    public Response revertCode(@QueryParam("historyID") Integer historyID) {
        IDEMetadata updatedMetadata = ideMetadataBean.revertToCodeHistory(historyID);
//        List<HistoryMetadata> historyMetadata = historyMetadataBean.getHistoryForExercise(ideMetadata.getExerciseID());
//        updatedMetadata.setCodeHistory(historyMetadata);
        return Response.ok(updatedMetadata).build();
    }

    private ApiError getNotFoundApiError(String message) {
        ApiError error = new ApiError();
        if (message.isEmpty()) message = "The exercise was not found";
        error.setCode(Response.Status.NOT_FOUND.toString());
        error.setMessage(message);
        error.setStatus(Response.Status.NOT_FOUND.getStatusCode());
        return error;
    }

}
