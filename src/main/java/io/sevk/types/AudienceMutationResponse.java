package io.sevk.types;

/** Audience projection returned by create and update operations. */
public class AudienceMutationResponse {
    public String id;
    public String name;
    public String description;
    public AudienceVisibility visibility;
    public String projectId;
    public String createdAt;
    public String updatedAt;
}
