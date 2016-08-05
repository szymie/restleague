package org.tiwpr.szymie.resources;

import org.tiwpr.szymie.daos.DaoFactory;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("test")
public class Test {

    @Inject
    DaoFactory daoFactory;

    @GET
    @Path(("t"))
    public void t() {
        System.out.println("test: " + daoFactory.toString());
    }

    public void setDaoFactory(DaoFactory daoFactory) {
        this.daoFactory = daoFactory;
    }
}
