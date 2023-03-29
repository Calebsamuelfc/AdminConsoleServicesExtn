package com.temenos.dbx.product.javaservice;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbputilities.util.logger.LoggerUtil;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.temenos.dbx.product.usermanagement.resource.api.InfinityUserManagementResource;
import com.temenos.dbx.product.usermanagement.resource.impl.InfinityUserManagementResourceImplExtn;
import com.temenos.dbx.product.usermanagement.resource.impl.ProfileManagementResourceImplExtn;
import org.apache.log4j.Logger;

public class EditInfinityUserOperationExtn implements JavaService2 {
    private static final Logger LOG = Logger.getLogger(ProfileManagementResourceImplExtn.class);

    public Object invoke(String methodId, Object[] inputArray, DataControllerRequest request, DataControllerResponse response) throws Exception {
        LOG.error("29032023 Invoke " );
        InfinityUserManagementResource infinityUserManagementResource = (InfinityUserManagementResource)DBPAPIAbstractFactoryImpl.getResource(InfinityUserManagementResource.class);
        return infinityUserManagementResource.editInfinityUser(methodId, inputArray, request, response);
    }
}