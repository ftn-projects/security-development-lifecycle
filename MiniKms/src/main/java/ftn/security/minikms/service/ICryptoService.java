package ftn.security.minikms.service;

import ftn.security.minikms.entity.KeyMaterial;

import java.security.NoSuchAlgorithmException;

public interface ICryptoService {
    KeyMaterial generateKey() throws NoSuchAlgorithmException;
}
