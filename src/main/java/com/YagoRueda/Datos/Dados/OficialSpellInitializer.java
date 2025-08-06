package com.YagoRueda.Datos.Dados;

import com.YagoRueda.Datos.Dados.enums.SpellDamageType;
import com.YagoRueda.Datos.Dados.models.SpellEntity;
import com.YagoRueda.Datos.Dados.models.UserEntity;
import com.YagoRueda.Datos.Dados.repositories.SpellRepository;
import com.YagoRueda.Datos.Dados.repositories.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

@Component
public class OficialSpellInitializer implements CommandLineRunner {

    private final SpellRepository spellRepository;
    private final UserRepository userRepository;
    private final String OFICIALUSERNAME = "Admin";

    public OficialSpellInitializer(SpellRepository spellRepository, UserRepository userRepository) {
        this.spellRepository = spellRepository;
        this.userRepository = userRepository;
    }


    @Override
    public void run(String... args) throws Exception {

        //Comprobación y creación del usuario
        System.out.println("🔍 Comprobando cuenta de hechizos oficiales...");
        if (!userRepository.existsByUsername(OFICIALUSERNAME)) {
            System.out.println("🔍 No existe cuenta de administración");
            System.out.println("Creando...");
            UserEntity user = new UserEntity();
            user.setUsername(OFICIALUSERNAME);
            user.setSignup_date(Instant.now());
            userRepository.save(user);
            System.out.println("Usuario Creado");
        } else {
            System.out.println(" El usuario existe");
        }

        UserEntity adminUser = userRepository.findByUsername(OFICIALUSERNAME);

        //
        RestTemplate restTemplate = new RestTemplate();
        ObjectMapper objectMapper = new ObjectMapper();

        // Obtener lista de hechizos
        String baseUrl = "https://www.dnd5eapi.co";
        String spellListUrl = baseUrl + "/api/spells";

        String spellListResponse = restTemplate.getForObject(spellListUrl, String.class);
        JsonNode spellListRoot = objectMapper.readTree(spellListResponse);

        System.out.println("🔍 Comprobando numero de hechizos");
        int oficialSpells = spellListRoot.get("count").asInt();
        long totalSpells = spellRepository.countByUser(adminUser);
        System.out.println("🔍 El número de hechizos coincide (" + totalSpells + "/" + oficialSpells + ").");


        if (oficialSpells!= totalSpells) {
            System.out.println("🔍 El número de hechizos no coincide (" + totalSpells + "/" + oficialSpells + "). Cargando hechizos oficiales...");

            restTemplate = new RestTemplate();
            objectMapper = new ObjectMapper();

            // Obtener lista de hechizos
            baseUrl = "https://www.dnd5eapi.co";
            spellListUrl = baseUrl + "/api/spells";

            spellListResponse = restTemplate.getForObject(spellListUrl, String.class);
            spellListRoot = objectMapper.readTree(spellListResponse);
            JsonNode results = spellListRoot.get("results");

            for (JsonNode spellSummary : results) {
                String spellUrl = baseUrl + spellSummary.get("url").asText();

                try {
                    String spellDetailsResponse = restTemplate.getForObject(spellUrl, String.class);
                    JsonNode spellNode = objectMapper.readTree(spellDetailsResponse);

                    if (!spellRepository.existsByUserAndName(adminUser, spellNode.get("name").asText().trim())) {
                        // Mapear campos relevantes (aquí debes adaptar esto a tu entidad SpellEntity)
                        SpellEntity spell = new SpellEntity();
                        spell.setName(spellNode.get("name").asText());
                        spell.setLevel(spellNode.get("level").asInt());
                        spell.setDescription(spellNode.get("desc").get(0).asText());
                        spell.setSchool(spellNode.get("school").get("name").asText());
                        spell.setComponents(spellNode.has("components") ? objectMapper.writeValueAsString(spellNode.get("components")) : "[]");
                        spell.setCastTime(spellNode.has("casting_time") ? spellNode.get("casting_time").asText() : "");
                        spell.setDuration(spellNode.has("duration") ? spellNode.get("duration").asText() : "");
                        spell.setCastRange(spellNode.has("range") ? spellNode.get("range").asText() : "");
                        spell.setConcentration(spellNode.has("concentration") && spellNode.get("concentration").asBoolean());
                        spell.setRitual(spellNode.has("ritual") && spellNode.get("ritual").asBoolean());
                        spell.setPublicVisible(true);
                        spell.setUser(adminUser);

                        if(spellNode.has("damage")){
                            JsonNode damage = spellNode.get("damage");

                            if (damage.has("damage_type")) {
                                String damageType = damage.get("damage_type").get("name").asText();
                                try {
                                    SpellDamageType type = SpellDamageType.valueOf(damageType.toUpperCase());
                                    spell.setDamageType(type);
                                } catch (IllegalArgumentException e) {
                                    System.err.println("Tipo de daño desconocido: " + damageType);
                                    System.err.println("Error al importar" + spellNode.get("name").asText());
                                }
                            }

                            if (damage.has("damage_at_slot_level")) {
                                JsonNode damageLevelNode = damage.get("damage_at_slot_level");
                                Map<Integer, String> damageMap = new HashMap<>();

                                Iterator<String> fieldNames = damageLevelNode.fieldNames();
                                while (fieldNames.hasNext()) {
                                    String level = fieldNames.next();
                                    String value = damageLevelNode.get(level).asText();
                                    damageMap.put(Integer.parseInt(level), value);
                                }

                                spell.setDamageByLevel(damageMap); // ✅ Correcto

                            }

                            // Alternativamente, daño por nivel de personaje (menos común)
                            else if (damage.has("damage_at_character_level")) {
                                JsonNode damageLevelNode = damage.get("damage_at_character_level");
                                Map<Integer, String> damageMap = new HashMap<>();

                                Iterator<String> fieldNames = damageLevelNode.fieldNames();
                                while (fieldNames.hasNext()) {
                                    String level = fieldNames.next();
                                    String value = damageLevelNode.get(level).asText();
                                    damageMap.put(Integer.parseInt(level), value);
                                }

                                spell.setDamageByLevel(damageMap);
                            }
                        }

                        // Guardar hechizo
                        spellRepository.save(spell);
                        System.out.println("✅ Hechizo guardado: " + spell.getName());
                    }


                } catch (Exception e) {
                    System.err.println("❌ Error procesando hechizo en: " + spellUrl);
                    e.printStackTrace();
                }
            }
            System.out.println("✅ Todos los hechizos oficiales han sido cargados.");
        } else {
            System.out.println("✅ Ya están todos los hechizos oficiales cargados.");
        }


    }
}
