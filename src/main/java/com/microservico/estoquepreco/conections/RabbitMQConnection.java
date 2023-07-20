package com.microservico.estoquepreco.conections;

import constantes.RabbitMQConstantes;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class RabbitMQConnection {

    private static final String NOME_EXCHANGE = "amq.direct";
    private AmqpAdmin amqpAdmin;

    public RabbitMQConnection(AmqpAdmin amqpAdmin) {
        this.amqpAdmin = amqpAdmin;
    }

    //metodo de gerar fila
    private Queue fila(String nomeFila) {
        return new Queue(nomeFila, true, false, false);
    }

    //metodo de gerar exchange
    private DirectExchange trocaDireta() {
        return new DirectExchange(NOME_EXCHANGE);
    }

    //metodo de criar o binding entre as filas e  exchange
    private Binding relacionamento(Queue fila, DirectExchange troca) {
        return new Binding(fila.getName(), Binding.DestinationType.QUEUE, troca.getName(), fila.getName(), null);
    }

    //adição das filas a exchange
    @PostConstruct
    private void adiciona() {
        Queue filaEstoque = this.fila(RabbitMQConstantes.FILA_ESTOQUE);
        Queue filaPreco = this.fila(RabbitMQConstantes.FILA_PRECO);

        DirectExchange troca = this.trocaDireta();

        Binding ligacaoEstoque = this.relacionamento(filaEstoque, troca);
        Binding ligacaoPreco = this.relacionamento(filaPreco, troca);

        //criando as filas no RabbitMQ
        this.amqpAdmin.declareQueue(filaEstoque);
        this.amqpAdmin.declareQueue(filaPreco);

        this.amqpAdmin.declareExchange(troca);
        this.amqpAdmin.declareBinding(ligacaoEstoque);
        this.amqpAdmin.declareBinding(ligacaoPreco);

    }
}
