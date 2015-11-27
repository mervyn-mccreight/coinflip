package de.fhwedel.coinflip.protocol.model;

import com.google.gson.annotations.SerializedName;
import de.fhwedel.coinflip.protocol.model.id.ProtocolId;
import de.fhwedel.coinflip.protocol.model.status.ProtocolStatus;

import java.util.List;

// todo (27.11.2015): redesign base protocol so it just has a the id, status and statusMessage
// fields.
// todo (27.11.2015): protocolNegotiation then moves to a subclass, as it represents the negotiation
// steps.
public class BaseProtocol {
  @SerializedName("protocolId")
  private ProtocolId id;

  @SerializedName("statusId")
  private ProtocolStatus status;

  private String statusMessage;

  private ProtocolNegotiation protocolNegotiation;

  public BaseProtocol(ProtocolId id, ProtocolStatus status, String statusMessage,
      ProtocolNegotiation protocolNegotiation) {
    this.id = id;
    this.status = status;
    this.statusMessage = statusMessage;
    this.protocolNegotiation = protocolNegotiation;
  }

  public int getStep() {
    return this.id.getId();
  }

  public int getStatus() {
    return this.status.getId();
  }

  public String getStatusMessage() {
    return this.statusMessage;
  }

  public String getNegotiatedVersion() {
    return protocolNegotiation.getNegotiatedVersion();
  }

  public List<Versions> getProposedVersions() {
    return protocolNegotiation.getAvailableVersions();
  }
}
