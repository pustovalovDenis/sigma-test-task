package ua.pustovalov.sigmatesttask.school.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ua.pustovalov.sigmatesttask.school.dto.StudentDto;

@Service
@Slf4j
public class KafkaProducer {

    public void sendMessageToHeadmaster(StudentDto studentDto) {
        log.warn("Sending message to headmaster.");
        log.warn(
            "Student with name {} surname {} and id: {} is unplaced.",
            studentDto.getName(),
            studentDto.getSurname(),
            studentDto.getId());
    }
}
