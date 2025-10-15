package com.ra.base_spring_boot.model;


import jakarta.persistence.*;
import lombok.Getter;  
import lombok.Setter;  

@Entity
@Table(name = "types_jobs")
@Getter 
@Setter 
public class TypeJobRelation {
    
    @Id
    private String id; 


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "job_id", referencedColumnName = "id")
  
    private Job job; 

   
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "type_job_id", referencedColumnName = "id") 

    private TypeJob typeJob; 
    
    
}