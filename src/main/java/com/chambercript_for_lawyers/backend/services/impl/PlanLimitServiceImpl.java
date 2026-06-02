package com.chambercript_for_lawyers.backend.services.impl;


import com.chambercript_for_lawyers.backend.model.LawFirm;
import com.chambercript_for_lawyers.backend.services.central.PlanLimitService;
import org.springframework.stereotype.Service;

@Service
public class PlanLimitServiceImpl implements PlanLimitService {

    public boolean isStorageLimitExceeded(LawFirm firm, long newFileSizeBytes) {
        long totalAfterUpload = firm.getUsedStorageBytes() + newFileSizeBytes;
        long limitInBytes = (long) firm.getSubscriptionPlan().getStorageGb() * 1024 * 1024 * 1024;
        return totalAfterUpload > limitInBytes;
    }

    public boolean isEmpLimitExceeded(LawFirm firm) {
        return firm.getUsedEmpAccounts() >= firm.getSubscriptionPlan().getMaxEmpAccounts();
    }

    public boolean isRecordLimitExceeded(LawFirm firm) {
        return firm.getUsedRecords() >= firm.getSubscriptionPlan().getMaxRecords();
    }
}