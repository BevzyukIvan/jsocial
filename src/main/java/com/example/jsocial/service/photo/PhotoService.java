package com.example.jsocial.service.photo;

import com.example.jsocial.dto.PhotoCardDTO;
import com.example.jsocial.dto.PhotoResponseDTO;
import com.example.jsocial.model.photo.Photo;
import com.example.jsocial.repository.photo.PhotoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PhotoService {

    private final PhotoRepository photoRepository;

    public PhotoService(PhotoRepository photoRepository) {
        this.photoRepository = photoRepository;
    }


    public void save(Photo photo) {
        photoRepository.save(photo);
    }

    public Photo findById(Long id) {
        return photoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Фото не знайдено"));
    }

    public void delete(Photo photo) {
        photoRepository.delete(photo);
    }

    public List<PhotoResponseDTO> findAllDTOs() {
        return photoRepository.findAllDTOs();
    }

    public PhotoResponseDTO findDTOById(Long id) {
        return photoRepository.findDTOById(id)
                .orElseThrow(() -> new RuntimeException("Фото не знайдено"));
    }
    public Page<PhotoCardDTO> getUserPhotoCards(String username, Pageable pageable) {
        return photoRepository.findCardsByOwner(username, pageable);
    }

}
