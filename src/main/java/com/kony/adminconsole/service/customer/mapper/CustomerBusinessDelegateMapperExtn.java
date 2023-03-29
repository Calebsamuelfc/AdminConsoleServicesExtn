package com.kony.adminconsole.service.customer.mapper;

import com.dbp.core.api.BusinessDelegate;
import com.dbp.core.api.DBPAPIMapper;
import com.kony.adminconsole.service.customer.businessdelegate.InfinityUserManagementBusinessDelegateImplExtn;
import com.kony.adminconsole.service.customer.businessdelegate.api.CustomerBusinessDelegate;
import com.kony.adminconsole.service.customer.businessdelegate.api.InfinityUserManagementBusinessDelegate;
import com.kony.adminconsole.service.customer.businessdelegate.api.PartyUserManagementBusinessDelegate;
import com.kony.adminconsole.service.customer.businessdelegate.impl.CustomerBusinessDelegateImpl;
import com.kony.adminconsole.service.customer.businessdelegate.impl.InfinityUserManagementBusinessDelegateImpl;
import com.kony.adminconsole.service.customer.businessdelegate.impl.PartyUserManagementBusinessDelegateImpl;
import java.util.HashMap;
import java.util.Map;

public class CustomerBusinessDelegateMapperExtn implements DBPAPIMapper<BusinessDelegate> {
    public Map<Class<? extends BusinessDelegate>, Class<? extends BusinessDelegate>> getAPIMappings() {
        Map<Class<? extends BusinessDelegate>, Class<? extends BusinessDelegate>> map = new HashMap<>();
        map.put(CustomerBusinessDelegate.class, CustomerBusinessDelegateImpl.class);
        map.put(InfinityUserManagementBusinessDelegate.class, InfinityUserManagementBusinessDelegateImplExtn.class);
        map.put(PartyUserManagementBusinessDelegate.class, PartyUserManagementBusinessDelegateImpl.class);
        return map;
    }
}
