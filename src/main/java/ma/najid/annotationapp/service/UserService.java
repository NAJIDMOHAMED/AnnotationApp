package ma.najid.annotationapp.service;

import ma.najid.annotationapp.Model.Annotator;
import ma.najid.annotationapp.repository.AnnotatorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private AnnotatorRepository annotatorRepository;

    public List<Annotator> getAllAnnotators() {
        return annotatorRepository.findAll();
    }
} 