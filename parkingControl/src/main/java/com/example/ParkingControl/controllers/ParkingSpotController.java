package com.example.ParkingControl.controllers;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.ParkingControl.dtos.ParkingSpotDtos;
import com.example.ParkingControl.models.ParkingSpotModel;
import com.example.ParkingControl.services.ParkingSpotService;

@RestController
@CrossOrigin (origins = "*")
@RequestMapping ("/parking-spot")
public class ParkingSpotController {

	final ParkingSpotService parkingSpotService;

	public ParkingSpotController(ParkingSpotService parkingSpotService) {
		super();
		this.parkingSpotService = parkingSpotService;
	}
	
	@PostMapping
	public ResponseEntity<Object> saveParkingSpot(@RequestBody @Valid ParkingSpotDtos parkingSpotDtos){		
		if(parkingSpotService.existsByPlateCar(parkingSpotDtos.getPlateCar()))  // validação para checar se ja existe a placa cadastrada
		{
			return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflict: License Plate Car is Already in use!");
		}
		if(parkingSpotService.existsByParkingSpotNumber(parkingSpotDtos.getParkingSpotNumber())) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflict: Parking Spot is already in use!");
		}
		if(parkingSpotService.existsByApartmentAndBlock(parkingSpotDtos.getApartment(),parkingSpotDtos.getBlock())) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflict: Parking Spot already registered for this block and apartment!");
		}		
		
		var parkingSpotModel = new ParkingSpotModel ();
		BeanUtils.copyProperties(parkingSpotDtos, parkingSpotModel);
		parkingSpotModel.setRegistrationDate(LocalDateTime.now(ZoneId.of("UTC")));
		return ResponseEntity.status(HttpStatus.CREATED).body(parkingSpotService.save(parkingSpotModel));		
	}
	@GetMapping
	public ResponseEntity<List<ParkingSpotModel>> getAllParkingSpots(){
		return ResponseEntity.status(HttpStatus.OK).body(parkingSpotService.findAll());
	}
	
	 @GetMapping("/{id}")
	    public ResponseEntity<Object> getOneParkingSpot(@PathVariable(value = "id") UUID id){
	        Optional<ParkingSpotModel> parkingSpotModelOptional = parkingSpotService.findById(id);
	        if (!parkingSpotModelOptional.isPresent()) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Parking Spot not found.");
	        }
	        return ResponseEntity.status(HttpStatus.OK).body(parkingSpotModelOptional.get());
	    }
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Object> deleteParkingSpot(@PathVariable (value = "id") UUID id){
		Optional<ParkingSpotModel> parkingSpotModelOptional = parkingSpotService.findById(id);
		
		if (!parkingSpotModelOptional.isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Parking Spot not found.");
			
		}
		parkingSpotService.delete(parkingSpotModelOptional.get());
		return ResponseEntity.status(HttpStatus.OK).body("Parking spot deleted successfully!");
	}
	
	
	
	
	
}
