package io.sevk.types;

import java.util.List;

public class BulkEmailResponse {
    public int success;
    public int failed;
    public List<String> ids;
    public List<BulkEmailError> errors;
}
