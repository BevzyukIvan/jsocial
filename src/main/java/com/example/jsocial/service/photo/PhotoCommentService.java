package com.example.jsocial.service.photo;

import com.example.jsocial.dto.PhotoCommentDTO;
import com.example.jsocial.model.photo.Photo;
import com.example.jsocial.model.photo.PhotoComment;
import com.example.jsocial.repository.photo.PhotoCommentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PhotoCommentService {

    private final PhotoCommentRepository photoCommentRepository;

    public PhotoCommentService(PhotoCommentRepository photoCommentRepository) {
        this.photoCommentRepository = photoCommentRepository;
    }

    public List<PhotoComment> getCommentsForPhoto(Long photoId) {
        Photo photo = new Photo();
        photo.setId(photoId);
        return photoCommentRepository.findByPhotoOrderByCreatedAtAsc(photo);
    }

    public PhotoComment findById(Long id) {
        return photoCommentRepository.findById(id).orElseThrow();
    }

    public void save(PhotoComment comment) {
        photoCommentRepository.save(comment);
    }

    public void delete(PhotoComment comment) {
        photoCommentRepository.delete(comment);
    }
    public List<PhotoCommentDTO> getCommentDTOs(Long photoId) {
        return photoCommentRepository.findCommentDTOsByPhotoId(photoId);
    }
}
