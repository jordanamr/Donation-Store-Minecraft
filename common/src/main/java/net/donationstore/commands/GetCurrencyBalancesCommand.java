package net.donationstore.commands;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.donationstore.dto.CurrencyBalanceDTO;
import net.donationstore.dto.GatewayResponse;
import net.donationstore.dto.InformationDTO;
import net.donationstore.dto.WebstoreAPIResponseDTO;
import net.donationstore.exception.InvalidCommandUseException;

import java.util.ArrayList;


public class GetCurrencyBalancesCommand extends AbstractApiCommand {

    @JsonProperty("uuid")
    private String uuid;

    @Override
    public String getSupportedCommand() {
        return "balance";
    }

    @Override
    public Command validate(String[] args) {
        if (args.length != 4) {
            getLogs().add(getInvalidCommandMessage());
            getLogs().add(helpInfo());
            throw new InvalidCommandUseException(getLogs());
        }

        getWebstoreHTTPClient().setSecretKey(args[0])
                .setWebstoreAPILocation(args[1]);

        setWebstoreAPIResponseDTO(CurrencyBalanceDTO.class);

        setUUID(args[3]);
        return this;
    }

    @Override
    public ArrayList<String> runCommand() throws Exception {

        GatewayResponse gatewayResponse = getWebstoreHTTPClient().post(this, "currency/balances");

        // Do stuff with the body
        return getLogs();
    }

    @Override
    public String helpInfo() {
        return "This command is used to view your Virtual Currency balances.";
    }

    @Override
    public CommandType commandType() {
        return CommandType.PLAYER;
    }

    public String getUuid() {
        return uuid;
    }

    public GetCurrencyBalancesCommand setUUID(String uuid) {
        this.uuid = uuid;
        return this;
    }
}
