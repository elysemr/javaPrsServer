package com.acme.prsserver.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
@RequestMapping("/api/requests")
public class RequestsController {
	
	@Autowired
	private RequestRepository reqRepo;
	
	@GetMapping
	public ResponseEntity<Iterable<Request>> GetAll() {
		var requests = reqRepo.findAll();
		return new ResponseEntity<Iterable<Request>>(requests, HttpStatus.OK);
	}
	
	@GetMapping("{id}")
	public ResponseEntity<Request> GetById(@PathVariable int id) {
		var request = reqRepo.findById(id);
		if(request.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<Request>(request.get(), HttpStatus.OK);
	}
	
	@GetMapping("user/{userId}")
	public ResponseEntity<Iterable<Request>> GetRequestsNotUser(@PathVariable int userId) {
		var requests = reqRepo.findByUserIdNot(userId);
		return new ResponseEntity<Iterable<Request>>(requests, HttpStatus.OK);
	}
	
	@PostMapping
	public ResponseEntity<Request> Insert(@RequestBody Request request) {
		if(request == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		request.setId(0);
		var newRequest = reqRepo.save(request);
		return new ResponseEntity<>(HttpStatus.CREATED);
	}
	
	@PutMapping("{id}")
	public ResponseEntity Update(@PathVariable int id, @RequestBody Request request) {
		if(request.getId() != id) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		reqRepo.save(request);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	@PutMapping("review/{id}")
	public ResponseEntity SetRequestToReview(@PathVariable int id, @RequestBody Request request) {
		var newStatus = request.getTotal() > 50.00 ? "REVIEW" : "APPROVED";
		request.setStatus(newStatus);
		return Update(id, request);
	}
	
	@PutMapping("approve/{id}")
	public ResponseEntity SetRequestToApproved(@PathVariable int id, @RequestBody Request request) {
				request.setStatus("APPROVED");
				return Update(id, request);
	}
	
	@PutMapping("reject/{id}")
	public ResponseEntity SetRequestToRejected(@PathVariable int id, @RequestBody Request request) {
		request.setStatus("REJECTED");
		return Update(id, request);
	}
	
	@DeleteMapping ("{id}")
	public ResponseEntity Delete(@PathVariable int id) {
		var request = reqRepo.findById(id);
		if(request.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		reqRepo.deleteById(id);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
