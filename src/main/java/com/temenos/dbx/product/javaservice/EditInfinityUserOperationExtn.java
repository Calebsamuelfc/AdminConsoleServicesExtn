package com.temenos.dbx.product.javaservice;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.temenos.dbx.product.usermanagement.resource.api.InfinityUserManagementResource;

public class EditInfinityUserOperationExtn implements JavaService2 {
    public Object invoke(String methodId, Object[] inputArray, DataControllerRequest request, DataControllerResponse response) throws Exception {
        InfinityUserManagementResource infinityUserManagementResource = (InfinityUserManagementResource)DBPAPIAbstractFactoryImpl.getResource(InfinityUserManagementResource.class);
        return infinityUserManagementResource.editInfinityUser(methodId, inputArray, request, response);
    }
}