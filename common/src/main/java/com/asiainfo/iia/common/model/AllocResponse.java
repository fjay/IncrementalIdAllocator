package com.asiainfo.iia.common.model;

/**
 * @author Jay Wu
 */
public class AllocResponse {

    private Long id;

    private ServerNodeRoute route;

    public Long getId() {
        return id;
    }

    public AllocResponse setId(Long id) {
        this.id = id;
        return this;
    }

    public ServerNodeRoute getRoute() {
        return route;
    }

    public AllocResponse setRoute(ServerNodeRoute route) {
        this.route = route;
        return this;
    }
}
