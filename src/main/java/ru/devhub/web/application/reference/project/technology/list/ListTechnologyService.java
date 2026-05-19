package ru.devhub.web.application.reference.project.technology.list;

import org.springframework.stereotype.Service;
import ru.devhub.web.application.common.CommandHandler;
import ru.devhub.web.domain.reference.project.technology.TechnologyPage;
import ru.devhub.web.domain.reference.project.technology.TechnologyRepository;

@Service
public class ListTechnologyService implements CommandHandler<ListTechnologyCommand, TechnologyPage> {
    private final TechnologyRepository technologyRepository;

    public ListTechnologyService(TechnologyRepository technologyRepository) {
        this.technologyRepository = technologyRepository;
    }

    @Override
    public TechnologyPage handle(ListTechnologyCommand command) {
        return technologyRepository.findPage();
    }
}