package com.fri.code.ide.api.v1.resources;

import com.fri.code.ide.api.v1.dtos.ApiError;
import com.fri.code.ide.lib.HistoryMetadata;
import com.fri.code.ide.lib.IDEMetadata;
import com.fri.code.ide.services.beans.HistoryMetadataBean;
import com.fri.code.ide.services.beans.IDEMetadataBean;
import com.kumuluz.ee.logs.cdi.Log;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.eclipse.microprofile.metrics.annotation.Timed;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.TreeSet;

@Log
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
    @Operation(summary = "Get all scripts", description = "Returns all scripts.",
            responses = {
                    @ApiResponse(description = "All scripts", responseCode = "200", content = @Content(array = @ArraySchema(schema = @Schema(implementation =
                            IDEMetadata.class))))
            }
    )
    @Timed
    public Response getAllScripts() {
        List<IDEMetadata> scripts = ideMetadataBean.getAllScripts();
        return Response.ok(scripts).build();
    }

    @GET
    @Operation(summary = "Get script details", description = "Returns script details for chosen exercise.")
    @ApiResponses({
            @ApiResponse(description = "Script details", responseCode = "200", content = @Content(array = @ArraySchema(schema = @Schema(implementation =
                    IDEMetadata.class))))
    })
    @Path("{exerciseID}")
    public Response getScriptsForExercise(@PathParam("exerciseID") Integer exerciseID) {
        IDEMetadata script = ideMetadataBean.getIDEForExercise(exerciseID);
        List<HistoryMetadata> historyMetadata = historyMetadataBean.getHistoryForExercise(exerciseID);
        script.setCodeHistory(historyMetadata);
        return Response.ok(script).build();
    }

    @PATCH
    @Operation(summary = "Change script code", description = "Returns updated script")
    @ApiResponses({
            @ApiResponse(description = "Updated script", responseCode = "200", content = @Content(schema = @Schema(implementation =
                    IDEMetadata.class))),
            @ApiResponse(description = "Script not found", responseCode = "404")
    })
    @Path("{scriptID}")
    public Response submitScript(@PathParam("scriptID") Integer scriptID, IDEMetadata ideMetadata) {
        ideMetadata = ideMetadataBean.updateIDEMetadata(scriptID, ideMetadata);
        if (ideMetadata == null) {
            return Response.status(Response.Status.NOT_FOUND).entity(getNotFoundApiError("", Response.Status.NOT_FOUND)).build();
        }
        historyMetadataBean.createHistoryMetadata(ideMetadata);
        List<HistoryMetadata> historyMetadata = historyMetadataBean.getHistoryForExercise(ideMetadata.getExerciseID());
        ideMetadata.setCodeHistory(historyMetadata);
        return Response.status(Response.Status.OK).entity(ideMetadata).build();
    }

    @POST
    @Operation(summary = "Submit script code", description = "Returns new script.")
    @ApiResponses({
            @ApiResponse(description = "New script", responseCode = "200", content = @Content(schema = @Schema(implementation =
                    IDEMetadata.class))),
            @ApiResponse(description = "Bad request", responseCode = "400")
    })
    public Response createInput(IDEMetadata ideMetadata) {
        ApiError error = new ApiError();
        error.setCode(Response.Status.BAD_REQUEST.toString());
        error.setMessage("You are missing some of the parameters");
        error.setStatus(Response.Status.BAD_REQUEST.getStatusCode());
        if (ideMetadata.getCode() == null || ideMetadata.getExerciseID() == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        } else {
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
    @Operation(summary = "Revert script code", description = "Returns script with previously submitted code")
    @ApiResponses({
            @ApiResponse(description = "Updated script", responseCode = "200", content = @Content(schema = @Schema(implementation =
                    IDEMetadata.class))),
            @ApiResponse(description = "History not found", responseCode = "404")
    })
    @Path("revert")
    public Response revertCode(@QueryParam("historyID") Integer historyID) {
        try {
            IDEMetadata updatedMetadata = ideMetadataBean.revertToCodeHistory(historyID);
            return Response.ok(updatedMetadata).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(getNotFoundApiError("History not found", Response.Status.NOT_FOUND)).build();
        }
    }

    private ApiError getNotFoundApiError(String message, Response.Status status) {
        ApiError error = new ApiError();
        if (message.isEmpty()) message = "The exercise was not found";
        error.setCode(status.toString());
        error.setMessage(message);
        error.setStatus(status.getStatusCode());
        return error;
    }

}
