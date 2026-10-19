package io.sevk.types;

import java.util.List;

public class BulkUpdateResponse {
    public int updated;
    public int notFound;
    public List<String> errors;
    public List<String> updatedEmails;
    public List<String> notFoundEmails;
}
