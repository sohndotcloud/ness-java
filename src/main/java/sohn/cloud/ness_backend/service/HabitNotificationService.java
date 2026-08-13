package sohn.cloud.ness_backend.service;

import sohn.cloud.ness_backend.entity.Habit;
import sohn.cloud.ness_backend.entity.SignalContact;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HabitNotificationService {

    private final SignalApiClient signalApiClient;

    public HabitNotificationService(SignalApiClient signalApiClient) {
        this.signalApiClient = signalApiClient;
    }

    public void notifyContacts(Habit habit, String message) {
        for (SignalContact contact : habit.getSignalContacts()) {
            signalApiClient.sendMessage(contact.getNumber(), message);
        }
    }

    public List<SignalContact> getContacts() {
        return signalApiClient.getContacts();
    }

}