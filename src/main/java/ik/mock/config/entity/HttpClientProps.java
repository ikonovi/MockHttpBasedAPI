package ik.mock.config.entity;

import lombok.Getter;

@Getter
public class HttpClientProps {
    Integer connectionRequestTimeoutMillis;
    Integer connectTimeoutMillis;
}
