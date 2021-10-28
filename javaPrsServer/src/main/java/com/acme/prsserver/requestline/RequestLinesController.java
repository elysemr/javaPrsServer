package com.acme.prsserver.requestline;

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

import com.acme.prsserver.product.ProductRepository;
import com.acme.prsserver.request.RequestRepository;

@CrossOrigin
@RestController
@RequestMapping("/api/requestlines")
public class RequestLinesController {
	
	@Autowired
	private RequestLineRepository rlRepo;
	@Autowired
	private RequestRepository reqRepo;
	@Autowired
	private ProductRepository prodRepo;
	
	@GetMapping
	public ResponseEntity<Iterable<RequestLine>> GetAll() {
		var requestLines = rlRepo.findAll();
		return new ResponseEntity<Iterable<RequestLine>>(requestLines, HttpStatus.OK);
		
	}
	
	@GetMapping("{id}")
	public ResponseEntity<RequestLine> GetById(@PathVariable int id) {
		var requestLine = rlRepo.findById(id);
		if(requestLine.isEmpty()) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<RequestLine>(requestLine.get(), HttpStatus.OK);
	}
	
	
	@PostMapping
		public ResponseEntity<RequestLine> Insert(@RequestBody RequestLine requestLine) throws Exception {
			if(requestLine == null) {
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			}
			requestLine.setId(0);
			var newRequestLine = rlRepo.save(requestLine);
			RecalculateRequest(requestLine.getRequest().getId());
			return new ResponseEntity<RequestLine>(newRequestLine, HttpStatus.CREATED);
		}
	
	@PutMapping("{id}")
	public ResponseEntity Update(@PathVariable int id, @RequestBody RequestLine requestLine) throws Exception {
		if(requestLine.getId() != id) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		rlRepo.save(requestLine);
		RecalculateRequest(requestLine.getRequest().getId());
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);

	}
	
	@DeleteMapping("{id}")
	public ResponseEntity Delete (@PathVariable int id) throws Exception {
		var requestLine = rlRepo.findById(id);
		if(requestLine.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		rlRepo.deleteById(id);
		RecalculateRequest(requestLine.get().getRequest().getId());
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	
	private void RecalculateRequest(int requestId) throws Exception {
		var optRequest = reqRepo.findById(requestId);
		if(optRequest.isEmpty()) {
			throw new Exception("Request ID is invalid.");
		}
		
		var request = optRequest.get();
		var requestLines = rlRepo.findRequestLineByRequestId(requestId);
		var total = 0.0;
		for(var reqLine : requestLines) {
			if(reqLine.getProduct().getPrice() == null) {
				var prodId = reqLine.getProduct().getId();
				var product = prodRepo.findById(prodId).get();
				reqLine.setProduct(product);
			}
			total += reqLine.getQuantity() * reqLine.getProduct().getPrice();
		}
		request.setTotal(total);
		reqRepo.save(request);
	}
	
	
	

}
