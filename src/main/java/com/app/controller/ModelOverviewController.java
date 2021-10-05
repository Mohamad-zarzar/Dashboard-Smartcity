package com.app.controller;

/**
 * ModelOverviewController
 * @param <T> Object
 */
public interface ModelOverviewController<T> extends OverviewController
{
    void setObj(T obj);
}
