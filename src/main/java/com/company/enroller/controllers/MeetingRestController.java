package com.company.enroller.controllers;

import com.company.enroller.model.Meeting;
import com.company.enroller.model.Participant;
import com.company.enroller.persistence.MeetingService;
import com.company.enroller.persistence.ParticipantService;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/meetings")
public class MeetingRestController {

    @Autowired
    MeetingService meetingService;
    ParticipantService participantService;
    
    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<?> getMeetings() {
        Collection<Meeting> meetings = meetingService.getAll();
        
        return new ResponseEntity<Collection<Meeting>>(meetings, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getMeeting(@PathVariable("id") long id) {
        if (meetingService.findById(id) == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Meeting meeting = meetingService.findById(id);
        return new ResponseEntity<Meeting>(meeting, HttpStatus.OK);
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<?> addMeeting(@RequestBody Meeting meeting) {
        if (meetingService.findById(meeting.getId()) == null) {
            return new ResponseEntity("Unable to create. Meeting already exists.", HttpStatus.CONFLICT);
        }
        meetingService.addMeeting(meeting);
        return new ResponseEntity<Meeting>(meeting, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> removeMeeting(@PathVariable("id") long id) {
        Meeting meeting = meetingService.findById(id);
        meetingService.removeMeeting(meeting);
        return new ResponseEntity("Meeting " + meeting.getTitle() + " removed.", HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}/addParticipant", method = RequestMethod.PUT)
    public ResponseEntity<?> addParticipantToMeeting(@PathVariable("id") long id, @RequestBody Participant participant) {
        Meeting meeting = meetingService.findById(id);
        if (meeting.getParticipants().contains(participant)) {
            return new ResponseEntity("Participant " + participant.getLogin() + " is already in the meeting.", HttpStatus.CONFLICT);
        }

        meeting.addParticipant(participant);
        meetingService.updateMeeting(meeting);
        return new ResponseEntity<Meeting>(meeting, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}/participants", method = RequestMethod.GET)
    public ResponseEntity<?> getMeetingParticipants(@PathVariable("id") long id) {
        return new ResponseEntity<Collection<Participant>>(meetingService.findById(id).getParticipants(), HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateMeeting(@PathVariable("id") long id, @RequestBody Meeting meeting) {
        meetingService.updateMeeting(meeting);
        return new ResponseEntity<Meeting>(meeting, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}/removeParticipant", method = RequestMethod.DELETE)
    public ResponseEntity<?> removeParticipantFromMeeting(@PathVariable("id") long id, @RequestBody Participant participant) {
        meetingService.findById(id).removeParticipant(participant);

        return new ResponseEntity<Meeting>(meetingService.findById(id), HttpStatus.OK);
    }
    
    @RequestMapping(value = "/sortByTitle", method = RequestMethod.GET)
    public ResponseEntity<?> sortMeetingsByTitle() {
        ArrayList<Meeting> meetingList = new ArrayList<>(meetingService.getAll());
        Collections.sort(meetingList);
        
        return new ResponseEntity<>(meetingList, HttpStatus.OK);
    }

    @RequestMapping(value = "/search/{login}", method = RequestMethod.GET)
    public ResponseEntity<?> findMeetingsByParticipant(@PathVariable("login") String login) {
        Participant p = participantService.findByLogin(login);
        ArrayList<Meeting> participantMeetings = new ArrayList<>();
        for (Meeting m : meetingService.getAll()) {
            if (m.getParticipants().contains(p)) {
                participantMeetings.add(m);
            }
        }
        Collections.sort(participantMeetings);
        if (participantMeetings.isEmpty()) {
            return new ResponseEntity("Participant " + login + "is in zero meetings.", HttpStatus.NOT_FOUND);
        }
        else {
            return new ResponseEntity<>(participantMeetings, HttpStatus.OK);
        }
        
    }
    
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public ResponseEntity<?> findMeetingsByTitleAndDescription(@RequestParam("title") String title, @RequestParam("description") String description) {
        ArrayList<Meeting> meetings = new ArrayList<>();
        for (Meeting m : meetingService.getAll()) {
            if (m.getTitle().contains(title) || m.getDescription().contains(description)) {
                meetings.add(m);
            }
        }
        Collections.sort(meetings);
        if (meetings.isEmpty()) {
            return new ResponseEntity("No meetings found containing the search string.", HttpStatus.NOT_FOUND);
        }
        else {
            return new ResponseEntity<>(meetings, HttpStatus.OK);
        }
        
    }

}
