package com.serhii.core.security;

import com.serhii.core.CoreEngine;
import com.serhii.core.security.impl.hash.HashBase;
import com.serhii.core.security.impl.hash.HashGenerator;

public class Hash implements HashBase {

    private HashGenerator generator;

    public Hash() {
        CoreEngine.getInstance().configure(this);
    }

    public void setGenerator(HashGenerator generator) {
        this.generator = generator;
    }

    @Override
    public String hashMD5(String message) {
        return generator.makeHashMD5(message);
    }
}