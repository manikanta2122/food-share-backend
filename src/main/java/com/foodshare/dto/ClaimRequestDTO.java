package com.foodshare.dto;

public class ClaimRequestDTO {

    // Claimant ID is optional since the authenticated user's identity is derived from SecurityContext
    private Long claimantId;

    private String note;

    private Integer estimatedPeopleFed;

    private String contactPerson;

    private String contactPhone;

    public ClaimRequestDTO() {}

    public Long getClaimantId() { return claimantId; }
    public void setClaimantId(Long claimantId) { this.claimantId = claimantId; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public Integer getEstimatedPeopleFed() { return estimatedPeopleFed; }
    public void setEstimatedPeopleFed(Integer estimatedPeopleFed) { this.estimatedPeopleFed = estimatedPeopleFed; }

    public String getContactPerson() { return contactPerson; }
    public void setContactPerson(String contactPerson) { this.contactPerson = contactPerson; }

    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }
}
