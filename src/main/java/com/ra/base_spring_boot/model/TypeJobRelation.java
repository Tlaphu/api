package com.ra.base_spring_boot.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;  

@Entity
@Table(name = "types_jobs")
@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder 
public class TypeJobRelation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long id;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "job_id", referencedColumnName = "id")
  
    private Job job; 

   
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "type_job_id", referencedColumnName = "id") 

    private TypeJob typeJob; 
    
    
}