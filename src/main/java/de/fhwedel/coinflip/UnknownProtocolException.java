package de.fhwedel.coinflip;

public class UnknownProtocolException extends Throwable {
  public UnknownProtocolException(Exception e) {
    super(e);
  }

  public UnknownProtocolException() {
    super();
  }
}
