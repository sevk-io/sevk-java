package io.sevk.types;

import java.util.List;

/** Broadcast projection returned by create operations. */
public class BroadcastWithVariants extends BroadcastBase {
    public List<BroadcastVariant> variants;
    public BroadcastAbTest abTest;
}
