package org.example.salon_project.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.salon_project.domain.*;
import org.example.salon_project.dto.ReviewCreateRequest;
import org.example.salon_project.dto.ReviewDto;
import org.example.salon_project.exception.ResourceNotFoundException;
import org.example.salon_project.mapper.ReviewMapper;
import org.example.salon_project.repository.ClientRepository;
import org.example.salon_project.repository.MasterRepository;
import org.example.salon_project.repository.ReviewRepository;
import org.example.salon_project.service.ReviewService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepo;
    private final ClientRepository clientRepo;
    private final MasterRepository masterRepo;
    private final ReviewMapper mapper;

    @Override
    @Transactional
    public ReviewDto create(ReviewCreateRequest request) {
        Client client = clientRepo.findById(request.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException("Client", request.getClientId()));
        Master master = masterRepo.findById(request.getMasterId())
                .orElseThrow(() -> new ResourceNotFoundException("Master", request.getMasterId()));
        Review review = new Review();
        review.setClient(client);
        review.setMaster(master);
        review.setRating(request.getRating().shortValue());
        review.setComment(request.getComment());
        return mapper.toDto(reviewRepo.save(review));
    }
}
