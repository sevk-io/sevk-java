package io.sevk.types;

import java.util.List;

/** Update response; experiment fields are present when the mutation touched them. */
public class BroadcastUpdate extends BroadcastBase {
    public List<BroadcastVariant> variants;
    public BroadcastAbTest abTest;
}
