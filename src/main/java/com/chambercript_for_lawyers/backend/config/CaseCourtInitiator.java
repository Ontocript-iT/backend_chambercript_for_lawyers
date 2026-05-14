package com.chambercript_for_lawyers.backend.config;


import com.chambercript_for_lawyers.backend.enums.CourtType;
import com.chambercript_for_lawyers.backend.model.CaseType;
import com.chambercript_for_lawyers.backend.model.Court;
import com.chambercript_for_lawyers.backend.repository.CaseTypeRepository;
import com.chambercript_for_lawyers.backend.repository.CourtRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class CaseCourtInitiator implements CommandLineRunner {

    private final CourtRepository courtRepository;

    private final CaseTypeRepository caseTypeRepository;

    @Override
    public void run(String... args) throws Exception {
        loadCourts();
        loadCaseTypes();
    }

    private void loadCourts() {
        // අලුතින් දත්ත ඇතුලත් කරන්නේ Database එක හිස් නම් පමණි
        if (courtRepository.count() == 0) {
            System.out.println("Seeding Sri Lankan Courts...");

            Court c1 = new Court(); c1.setCourtName("Supreme Court of Sri Lanka"); c1.setCourtType(CourtType.SUPREME); c1.setLocation("Colombo 12");
            Court c2 = new Court(); c2.setCourtName("Court of Appeal"); c2.setCourtType(CourtType.APPEAL); c2.setLocation("Colombo 12");
            Court c3 = new Court(); c3.setCourtName("High Court - Colombo"); c3.setCourtType(CourtType.HIGH); c3.setLocation("Colombo 12");
            Court c4 = new Court(); c4.setCourtName("High Court - Kandy"); c4.setCourtType(CourtType.HIGH); c4.setLocation("Kandy");
            Court c5 = new Court(); c5.setCourtName("District Court - Colombo"); c5.setCourtType(CourtType.DISTRICT); c5.setLocation("Colombo 12");
            Court c6 = new Court(); c6.setCourtName("District Court - Gampaha"); c6.setCourtType(CourtType.DISTRICT); c6.setLocation("Gampaha");
            Court c7 = new Court(); c7.setCourtName("Magistrate's Court - Colombo"); c7.setCourtType(CourtType.MAGISTRATE); c7.setLocation("Colombo");
            Court c8 = new Court(); c8.setCourtName("Labour Tribunal - Colombo"); c8.setCourtType(CourtType.LABOUR); c8.setLocation("Colombo");

            courtRepository.saveAll(Arrays.asList(c1, c2, c3, c4, c5, c6, c7, c8));
        }
    }

    private void loadCaseTypes() {
        if (caseTypeRepository.count() == 0) {
            System.out.println("Seeding Sri Lankan Case Types...");


            CaseType civil = new CaseType(); civil.setTypeName("Civil"); civil.setDescription("All civil disputes");
            CaseType criminal = new CaseType(); criminal.setTypeName("Criminal"); criminal.setDescription("All criminal offenses");
            CaseType fr = new CaseType(); fr.setTypeName("Fundamental Rights"); fr.setDescription("FR cases filed in Supreme Court");

            caseTypeRepository.saveAll(Arrays.asList(civil, criminal, fr));

            CaseType property = new CaseType(); property.setTypeName("Property & Land"); property.setDescription("Partition, Deeds, Boundaries"); property.setParentCategory(civil);
            CaseType family = new CaseType(); family.setTypeName("Family & Divorce"); property.setDescription("Divorce, Child Custody"); family.setParentCategory(civil);
            CaseType money = new CaseType(); money.setTypeName("Money Recovery"); property.setDescription("Debt recovery, Cheque bounces"); money.setParentCategory(civil);

            CaseType murder = new CaseType(); murder.setTypeName("Murder & Assault"); property.setDescription("Serious bodily harm"); murder.setParentCategory(criminal);
            CaseType fraud = new CaseType(); fraud.setTypeName("Fraud & Financial Crimes"); property.setDescription("Scams, Embezzlement"); fraud.setParentCategory(criminal);

            caseTypeRepository.saveAll(Arrays.asList(property, family, money, murder, fraud));
        }
    }
}