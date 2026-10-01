package org.example.backendweride.platform.iam.infrastructure.hashing.bcrypt;

import org.example.backendweride.platform.iam.application.internal.outboundservices.hashing.HashingService;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * BCryptHashingService interface that extends both HashingService and PasswordEncoder.
 * This interface provides methods for hashing and verifying passwords using the BCrypt algorithm.
 */
public interface BCryptHashingService extends HashingService, PasswordEncoder {}
