package com.example.maternity.dao;

import com.example.maternity.dto.*;
import com.example.maternity.model.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.ArrayList;
import java.util.List;


public class PatientResultDAO {
	protected static EntityManagerFactory emf = Persistence.createEntityManagerFactory("maternity");


	public PatientResultDAO() {
		
	}

	public PatientResult getPatientResult(Integer patientResultId) {
		EntityManager em = emf.createEntityManager();
		PatientResult patient = (PatientResult) em.createNamedQuery("PatientResult.findByPatientResultId",PatientResult.class)
				.setParameter("patient_resultID", patientResultId)
				.getSingleResult();
		em.close();
		return patient;
	}
	public PatientResult persistPatient(PatientResult patientResult) {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		em.persist(patientResult);
		em.getTransaction().commit();
		em.close();
		
		return patientResult;
	}


	public PatientResultDto getPatientResultById(Integer patientId) {
		PatientResultDto patientResultDto = new PatientResultDto();
		ResultDetailsDAO resultDetailsDAO = new ResultDetailsDAO();
		ResultDAO resultDAO = new ResultDAO();
		PatientDto patientDto = new PatientDto();
		PatientDAO patientDAO = new PatientDAO();
		Patient patient = patientDAO.getPatientById(patientId);
		System.out.println("First Name "+patient.getFirstName());

		patientDto.setPatientId(patient.getPatientID());
		patientDto.setFirstName(patient.getFirstName());
		patientDto.setLastName(patient.getLastName());
		patientDto.setFin(patient.getFin());
		patientDto.setMrn(patient.getMrn());
		patientResultDto.setPatient(patientDto);
		List<CategoryDto> categoryDtoList = new ArrayList<>();
	if(null!= patient){
			List<Child> childsList=  patient.getChilds();
			List<ChildDto> childDtosList = new ArrayList<>();
			for(Child child :childsList){
				ChildDto childDto = new ChildDto();
				childDto.setChildID(child.getChildID());
				childDto.setMrn(child.getMrn());
				childDto.setFirstName(child.getFirstName());
				childDto.setLastName(child.getLastName());

				childDtosList.add(childDto);

				System.out.println("Child FirstName :"+child.getFirstName());
				System.out.println("Child LastName :"+child.getLastName());
				List<BabyResultTable> babyResultTables = child.getBabyResultTables();
				for(BabyResultTable babyResultTable :babyResultTables) {
					System.out.println("CategoryName :"+babyResultTable.getCategory_name());
					System.out.println("DateTime :"+babyResultTable.getDateTime());
					patientResultDto.setResultCopiedDateTime(babyResultTable.getDateTime());
				}

				CategoryDto categoryDto = null;
				if(babyResultTables.size()==0){
					CategoryDAO categoryDAO = new CategoryDAO();
					List<Category> categoryList = categoryDAO.getCategories();

					for(Category category:categoryList){
						categoryDto = new CategoryDto();
						categoryDto.setCategoryID(category.getCategoryID());
						categoryDto.setCategory_name(category.getCategory_name());
						List<Result> resultList = resultDAO.getCategories(category.getCategoryID());
						List<ResultDto> resultDtoList = new ArrayList<>();
						for(Result result :resultList){
							ResultDto resultDto = new ResultDto();
							resultDto.setResultId(result.getResultID());
							resultDto.setResultName(result.getResult_name());
                            							int resultId = result.getResultID();  //20

							Result rslt = resultDAO.getResultById(resultId);

							List<PatientResult> prList = patient.getPatientResults();

							for(PatientResult patientResult :prList){

								ResultDetail rd = resultDetailsDAO.getResultDetails(patientResult,result);
							    if(null!=rd){
									String val = rd.getPatientResult1().getValue();
									resultDto.setValue(val);
									resultDtoList.add(resultDto);
								}
							}


							//ResultDetail rd = resultDetailsDAO.getResultDetailByResult(rslt);

							//resultDetailsDAO.getResultDetailByResult(result);
							// get result_details.PatientResult from result_details where resultId= 20



						}
						categoryDto.setResult(resultDtoList);
						categoryDtoList.add(categoryDto);
					}
				}
			}
            System.out.println(childDtosList.size());
		    //patientResultDto.set
			patientResultDto.setChild(childDtosList);
		    patientResultDto.setCategory(categoryDtoList);
		}



		return patientResultDto;
	}


}
