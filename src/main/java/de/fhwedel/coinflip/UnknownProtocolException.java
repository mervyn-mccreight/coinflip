package de.fhwedel.coinflip;

public class UnknownProtocolException extends Exception {
  public UnknownProtocolException(Exception e) {
    super(e);
  }

  public UnknownProtocolException() {
    super();
  }
}
