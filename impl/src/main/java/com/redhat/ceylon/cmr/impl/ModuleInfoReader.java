package com.redhat.ceylon.cmr.impl;

import java.io.File;
import java.util.Set;

import com.redhat.ceylon.cmr.api.ModuleVersionDetails;

public interface ModuleInfoReader {

    public boolean matchesModuleInfo(String moduleName, File moduleArchive, String query);

    public ModuleVersionDetails readModuleInfo(String moduleName, File moduleArchive);

    public int[] getBinaryVersions(String moduleName, File moduleArchive);

    public Set<String> getMembers(String moduleName, File moduleArchive);

}
