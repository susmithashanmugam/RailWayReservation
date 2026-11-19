
package com.railway.trainservice.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.railway.trainservice.dto.TrainSearchResponse;
import com.railway.trainservice.entity.Train;
import com.railway.trainservice.entity.TrainStop;
import com.railway.trainservice.repository.TrainStopRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TrainSearchService {

    private final TrainStopRepository trainStopRepository;
    private final Executor trainSearchExecutor;

    public TrainSearchService(
            TrainStopRepository trainStopRepository,
            @Qualifier("trainSearchExecutor") Executor trainSearchExecutor) {

        this.trainStopRepository = trainStopRepository;
        this.trainSearchExecutor = trainSearchExecutor;
    }

    public CompletableFuture<List<TrainSearchResponse>> searchTrains(
            Long sourceStationId,
            Long destinationStationId) {

        log.info("Train search started: source={}, destination={}",
                sourceStationId, destinationStationId);

        if (sourceStationId == null || destinationStationId == null) {
            log.warn("Train search rejected: station IDs are missing");
            throw new IllegalArgumentException(
                    "Source and destination station IDs are required");
        }

        if (sourceStationId.equals(destinationStationId)) {
            log.warn("Train search rejected: same source and destination");
            throw new IllegalArgumentException(
                    "Source and destination stations cannot be the same");
        }

        List<Train> trains =
                trainStopRepository.findTrainsBetweenStations(
                        sourceStationId,
                        destinationStationId);

        log.info("Found {} matching trains", trains.size());

        List<CompletableFuture<TrainSearchResponse>> futures =
                trains.stream()
                        .map(train -> CompletableFuture.supplyAsync(
                                () -> buildSearchResponse(
                                        train,
                                        sourceStationId,
                                        destinationStationId),
                                trainSearchExecutor))
                        .toList();

        CompletableFuture<Void> allTasks =
                CompletableFuture.allOf(
                        futures.toArray(new CompletableFuture[0]));

        return allTasks.thenApply(ignored -> {
            List<TrainSearchResponse> results =
                    futures.stream()
                            .map(CompletableFuture::join)
                            .toList();

            log.info("Train search completed successfully. Results: {}",
                    results.size());

            return results;
        }).whenComplete((result, exception) -> {
            if (exception != null) {
                log.error("Train search failed", exception);
            }
        });
    }

    private TrainSearchResponse buildSearchResponse(
            Train train,
            Long sourceStationId,
            Long destinationStationId) {

        log.debug("Processing train ID: {}", train.getId());

        List<TrainStop> stops =
                trainStopRepository
                        .findByTrain_IdOrderByStopNumberAsc(
                                train.getId());

        TrainStop source = stops.stream()
                .filter(stop -> stop.getStation().getId()
                        .equals(sourceStationId))
                .findFirst()
                .orElseThrow();

        TrainStop destination = stops.stream()
                .filter(stop -> stop.getStation().getId()
                        .equals(destinationStationId))
                .findFirst()
                .orElseThrow();

        return new TrainSearchResponse(
                train.getId(),
                train.getTrainNumber(),
                train.getTrainName(),
                train.getTrainType(),
                source.getStation().getId(),
                source.getStation().getStationName(),
                source.getDepartureTime(),
                destination.getStation().getId(),
                destination.getStation().getStationName(),
                destination.getArrivalTime()
        );
    }
}
