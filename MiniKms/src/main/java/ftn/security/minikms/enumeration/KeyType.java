package ftn.security.minikms.enumeration;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum KeyType {
    SYMMETRIC, ASYMMETRIC, HMAC;
}
