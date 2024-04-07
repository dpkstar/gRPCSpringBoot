package com.optum.comlayer.cloud;
import reactor.core.publisher.Mono;

public interface OnCloudService {
    Mono<String> sendJsonToOnCloud(String json);
}
