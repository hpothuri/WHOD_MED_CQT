package com.dbms.csmq.model.releaseGroups;

import oracle.jbo.server.ApplicationModuleImpl;
// ---------------------------------------------------------------------
// ---    File generated by Oracle ADF Business Components Design Time.
// ---    Mon May 09 10:41:48 EDT 2011
// ---    Custom code may be added to this class.
// ---    Warning: Do not modify method signatures of generated methods.
// ---------------------------------------------------------------------
public class ReleaseGroupModuleImpl extends ApplicationModuleImpl {
    /**
     * This is the default constructor (do not remove).
     */
    public ReleaseGroupModuleImpl() {
    }

    /**
     * Container's getter for releaseGroups1.
     * @return releaseGroups1
     */
    public ReleaseGroupsVOImpl getreleaseGroups1() {
        return (ReleaseGroupsVOImpl)findViewObject("releaseGroups1");
    }
}