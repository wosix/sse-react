import React, { useEffect, useRef, useState } from "react";
import Button from '@mui/material/Button';
import { Box } from "@mui/system";
import { useSnackbar } from 'notistack';

const SSEEvents = () => {
    const [isConnected, setIsConnected] = useState();
    const eventSourceRef = useRef(null);

    const { enqueueSnackbar } = useSnackbar();

    const connectSSE = () => {
        if (eventSourceRef.current) return;

        const eventSource = new EventSource('http://localhost:8080/events');
        eventSourceRef.current = eventSource;

        eventSource.onopen = () => {
            console.log('Połączenie SSE nawiązane!');
            setIsConnected(true);
        };

        eventSource.onmessage = (event) => {
            console.log('Received event:', event.data);
            enqueueSnackbar(event.data, 'success');
        };

        eventSource.onerror = (error) => {
            console.error('EventSource failed:', error);
            disconnectSSE();
        };
    }

    const disconnectSSE = () => {
        eventSourceRef.current?.close();
        eventSourceRef.current = null;
        setIsConnected(false);
        console.log('Połączenie SSE zamknięte!');
    }

    useEffect(() => {
        return () => disconnectSSE();
    }, [])


    return <>

        <Box sx={{ width: 'min-content', margin: 'auto', paddingTop: '380px' }}>
            {isConnected ?
                <Button color="warning" variant="contained"
                    onClick={() => disconnectSSE()}
                >
                    rozłącz
                </Button>
                :
                <Button color="success" variant="contained"
                    onClick={() => connectSSE()}
                >
                    połącz
                </Button>
            }
            <Button color="inherit" variant="contained"
                onClick={() => enqueueSnackbar('siema')}
            >
                połącz
            </Button>
        </Box>

        {/* {messages.map((message, index) => {
            <AlertBar
                key={index}
                message={message}
            />
        })} */}

    </>
}

export default SSEEvents;