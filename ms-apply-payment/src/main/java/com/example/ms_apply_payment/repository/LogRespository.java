package com.example.ms_apply_payment.repository;

import com.example.ms_apply_payment.entities.Log;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogRespository extends JpaRepository<Log, Long> {

}
