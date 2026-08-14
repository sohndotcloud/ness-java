package sohn.cloud.ness_backend.service;

import sohn.cloud.ness_backend.entity.Habit;
import sohn.cloud.ness_backend.entity.SignalContact;
import org.springframework.stereotype.Service;
import sohn.cloud.ness_backend.repo.SignalContactRepository;

import java.util.List;

@Service
public class HabitNotificationService {

    private final SignalApiClient signalApiClient;
    private final SignalContactRepository signalContactRepository;

    public HabitNotificationService(SignalApiClient signalApiClient,
                                    SignalContactRepository signalContactRepository) {
        this.signalApiClient = signalApiClient;
        this.signalContactRepository = signalContactRepository;
    }

    public SignalContact saveContact(String number, String name) {
        SignalContact contact = new SignalContact();
        contact.setNumber(number);
        contact.setName(name);
        return signalContactRepository.save(contact);
    }

    public void notifyContacts(String phoneNumber, Habit habit, String message) {
        for (SignalContact contact : habit.getSignalContacts()) {
            signalApiClient.sendMessage(phoneNumber, contact.getNumber(), message);
        }
    }

    public List<SignalContact> getContacts(String phoneNumber) {
        return signalApiClient.getContacts(phoneNumber);
    }

}