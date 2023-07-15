package com.driver.services.impl;

import com.driver.model.TripBooking;
import com.driver.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.driver.model.Customer;
import com.driver.model.Driver;
import com.driver.repository.CustomerRepository;
import com.driver.repository.DriverRepository;
import com.driver.repository.TripBookingRepository;
import com.driver.model.TripStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerServiceImpl implements CustomerService {

	@Autowired
	CustomerRepository customerRepository2;

	@Autowired
	DriverRepository driverRepository2;

	@Autowired
	TripBookingRepository tripBookingRepository2;

	@Override
	public void register(Customer customer) {
		//Save the customer in database
		customerRepository2.save(customer);
	}

	@Override
	public void deleteCustomer(Integer customerId) {
		// Delete customer without using deleteById function
		List<Customer> list = customerRepository2.findAll();
		for(Customer customer : list){
			if(customer.getCustomerId() == customerId) customerRepository2.delete(customer);
		}

	}

	@Override
	public TripBooking bookTrip(int customerId, String fromLocation, String toLocation, int distanceInKm) throws Exception{
		//Book the driver with lowest driverId who is free (cab available variable is Boolean.TRUE). If no driver is available, throw "No cab available!" exception
		//Avoid using SQL query
//

//		if(optionalCustomer.isPresent()){
//
//			TripBooking tripBooking = new TripBooking();
//			tripBooking.setToLocation(toLocation);
//			tripBooking.setFromLocation(fromLocation);
//			tripBooking.setDistanceInKm(distanceInKm);
//
//		}
		List<Driver> list = driverRepository2.findAll();
		boolean flag = false;
		int num = Integer.MAX_VALUE;
		Driver d = null;
		int price = 1;
		for(Driver driver : list){
			if(driver.getDriverId()<num && driver.getCab().getAvailable() == true)  {
				num = driver.getDriverId();
				//driver.getCab().setAvailable(false);
				price = driver.getCab().getPerKmRate();
				d = driver;
				flag = true;

			}
		}
		if(flag==false) throw new Exception("No cab available!");

     TripBooking tripBooking = new TripBooking();
		Customer customer = customerRepository2.findById(customerId).get();
	 tripBooking.setFromLocation(fromLocation);
	 tripBooking.setToLocation(toLocation);
	 tripBooking.setDistanceInKm(distanceInKm);
	 tripBooking.setStatus(TripStatus.CONFIRMED);
	 tripBooking.setBill(distanceInKm*price);
	 tripBooking.setCustomer(customer);
	 tripBooking.setDriver(d);
	 d.getTripBookingList().add(tripBooking);
	 customer.getTripBookingList().add(tripBooking);
    customerRepository2.save(customer);
	driverRepository2.save(d);


		 return tripBooking;
	}

	@Override
	public void cancelTrip(Integer tripId){
		//Cancel the trip having given trip Id and update TripBooking attributes accordingly
		Optional<TripBooking> tripBooking = tripBookingRepository2.findById(tripId);
		if(tripBooking.isPresent()){
			TripBooking trip = tripBooking.get();
			trip.setStatus(TripStatus.CANCELED);
			trip.setBill(0);
			trip.getDriver().getCab().setAvailable(true);
			tripBookingRepository2.save(trip);

		}

	}

	@Override
	public void completeTrip(Integer tripId){
		//Complete the trip having given trip Id and update TripBooking attributes accordingly
		Optional<TripBooking> tripBooking = tripBookingRepository2.findById(tripId);
		if(tripBooking.isPresent()){
			TripBooking trip = tripBooking.get();
			trip.setStatus(TripStatus.COMPLETED);
			tripBookingRepository2.save(trip);
		}
	}
}
