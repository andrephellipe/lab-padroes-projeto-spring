package one.digitalinnovation.gof.service.impl;

import one.digitalinnovation.gof.model.Cliente;
import one.digitalinnovation.gof.model.ClienteRepository;
import one.digitalinnovation.gof.model.Endereco;
import one.digitalinnovation.gof.model.EnderecoRepository;
import one.digitalinnovation.gof.service.ClienteService;
import one.digitalinnovation.gof.service.ViaCepService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implementação da <b>Strategy</b>{@link ClienteService}, a qual pode ser injetada pelo Spring via
 * {@link Autowired}. Com isso, como essa classe é um {@link Service}, ela será tratada como um
 * <b>Singleton</b>
 *
 * @author André
 *
 */

@Service
public class ClienteServiceImpl implements ClienteService {

    // Singleton: injetar os componentes do Spring com @Autowired.
    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private EnderecoRepository enderecorepository;

    @Autowired
    private ViaCepService viaCepService;

    //Strategy:Implementar os métodos definidos na interface

    //Facade: Abstrair integrações com subsistemas, provendo um inferface simples.

    @Override
    public Iterable<Cliente> buscarTodos() {
        return clienteRepository.findAll();
    }

    @Override
    public Cliente buscarPorId(Long id) {
        Optional<Cliente> findById = clienteRepository.findById(id);
        return findById.get();
    }

    @Override
    public void inserir(Cliente cliente) {
        salvarClienteComCep(cliente);
    }

    private void salvarClienteComCep(Cliente cliente) {
        //Verificar o o endereço do cliente já existe(via cep)
        String cep = cliente.getEndereco().getCep();
        Endereco endereco = enderecorepository.findById(cep).orElseGet(() -> {
            //Caso não exista, integrar com viaCep e persistir o retorno
            Endereco novoEndereco = viaCepService.consultarCep(cep);
            enderecorepository.save(novoEndereco);
            return novoEndereco;
        });
        cliente.setEndereco(endereco);
        //Caso exista, inserir o  cliente viculado ao endereço novo ou existente
        clienteRepository.save(cliente);
    }

    @Override
    public void atualizar(Long id, Cliente cliente) {
        // Buscar cliente pelo id caso exista
        Optional<Cliente> clienteID = clienteRepository.findById(id);
        if (clienteID.isPresent()) {
            salvarClienteComCep(cliente);
        }
    }

    @Override
    public void deletar(Long id) {
        Optional<Cliente> clienteID = clienteRepository.findById(id);
        if (clienteID.isPresent()) {
            clienteRepository.deleteById(id);
        }
    }
}
