package com.likelion.hackatonbe.domain.user.repository;

import com.likelion.hackatonbe.domain.user.entity.Child;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChildRepository extends JpaRepository<Child, Long> {
}