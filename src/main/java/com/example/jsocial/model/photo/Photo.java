package com.example.jsocial.model.photo;

import com.example.jsocial.model.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Photo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String url;

    private LocalDateTime uploadedAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Size(max = 1000, message = "Опис не повинен перевищувати 1000 символів")
    @Column(nullable = false, length = 1000)
    private String description;

    @OneToMany(mappedBy = "photo", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<PhotoComment> comments = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        uploadedAt = LocalDateTime.now();
    }
}
