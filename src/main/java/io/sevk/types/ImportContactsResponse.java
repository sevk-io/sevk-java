package io.sevk.types;

import java.util.List;

public class ImportContactsResponse {
    public int created;
    public List<String> errors;
    public List<ImportContactsResponseContact> contacts;
}
