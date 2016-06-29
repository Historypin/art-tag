//package sk.eea.arttag.controller;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.ResponseBody;
//import org.thymeleaf.util.StringUtils;
//import sk.eea.arttag.ApplicationProperties;
//import sk.eea.arttag.model.CulturalObject;
//import sk.eea.arttag.model.CulturalObjectDTO;
//import sk.eea.arttag.model.LocalizedString;
//import sk.eea.arttag.repository.CulturalObjectRepository;
//
//import java.util.List;
//import java.util.Locale;
//import java.util.Optional;
//
//@Controller
//public class CulturalObjectsDetailsController {
//
//    @Autowired
//    private ApplicationProperties applicationProperties;
//
//    @Autowired
//    private CulturalObjectRepository culturalObjectRepository;
//
//    @RequestMapping("/cultural-object-detail/{id}")
//    public @ResponseBody CulturalObjectDTO getDetail(@PathVariable Long objectId) {
//        final CulturalObjectDTO culturalObjectDTO = new CulturalObjectDTO();
//        final CulturalObject culturalObject = culturalObjectRepository.findOne(objectId);
//        if(culturalObject != null) {
//            culturalObjectDTO.setAuthor(culturalObject.getAuthor());
//            culturalObjectDTO.setUrl(culturalObject.getExternalUrl());
//            // TODO: refactor hardcoded language
//            final String language = Locale.ENGLISH.getLanguage();
//
//            final List<LocalizedString> localizedStrings = culturalObject.getDescription();
//            if (localizedStrings != null && !localizedStrings.isEmpty()) {
//                // first we prefer description in users language
//                Optional<LocalizedString> localizedString = localizedStrings.stream().filter(e -> e.getLanguage().equals(language)).findFirst();
//                if(localizedString.isPresent()) {
//                    culturalObjectDTO.setDescription(StringUtils.substring(localizedString.get().getValue(), 0, applicationProperties.getMaxDescriptionSize()));
//                    return culturalObjectDTO;
//                }
//
//                // then we prefer EN description
//                localizedString = localizedStrings.stream().filter(e -> e.getLanguage().equals(Locale.ENGLISH.getLanguage())).findFirst();
//                if(localizedString.isPresent()) {
//                    culturalObjectDTO.setDescription(String.format("(%s): %s", Locale.ENGLISH.getLanguage(), StringUtils.substring(localizedString.get().getValue(), 0, applicationProperties.getMaxDescriptionSize())));
//                    return culturalObjectDTO;
//                }
//
//                // otherwise any language
//                localizedString = localizedStrings.stream().findFirst();
//                if(localizedString.isPresent()) {
//                    culturalObjectDTO.setDescription(String.format("(%s): %s", localizedString.get().getLanguage(), StringUtils.substring(localizedString.get().getValue(), 0, applicationProperties.getMaxDescriptionSize())));
//                }
//            }
//        }
//        return culturalObjectDTO;
//    }
//
//}
