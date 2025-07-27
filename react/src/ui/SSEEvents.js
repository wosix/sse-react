import React, { useEffect, useRef, useState } from "react";
import Button from '@mui/material/Button';
import { Box } from "@mui/system";
import { defaultSnackbar, errorSnackbar, infoSnackbar, successSnackbar, warningSnackbar } from "./AlertBars";

const SSEEvents = () => {
    const [isConnected, setIsConnected] = useState();
    const eventSourceRef = useRef(null);

    const connectSSE = () => {
        if (eventSourceRef.current) return;

        const eventSource = new EventSource('http://localhost:8080/events');
        eventSourceRef.current = eventSource;

        eventSource.onopen = () => {
            console.log('Połączenie SSE nawiązane!');
            successSnackbar('Połączenie SSE nawiązane!')
            setIsConnected(true);
        };

        eventSource.onmessage = (event) => {
            console.log('Received event:', event.data);
            infoSnackbar(event.data);
        };

        eventSource.onerror = (error) => {
            console.error('EventSource failed:', error);
            errorSnackbar('EventSource failed')
            disconnectSSE();
        };
    }

    const disconnectSSE = () => {
        eventSourceRef.current?.close();
        eventSourceRef.current = null;
        setIsConnected(false);
        console.log('Połączenie SSE zamknięte!');
        warningSnackbar('Połączenie SSE zamknięte!')
    }

    useEffect(() => {
        return () => disconnectSSE();
    }, [])


    return <>
        <Box sx={{ display: 'flex', justifyContent: 'center', paddingTop: '380px', gap: '10px' }}>
            {isConnected ?
                <Button color="warning" variant="contained" size="small"
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
                onClick={() => defaultSnackbar('default message')}
            >
                default
            </Button>
            <Button color="warning" variant="contained"
                onClick={() => warningSnackbar('warrning message')}
            >
                warning
            </Button>
            <Button color="error" variant="contained"
                onClick={() => errorSnackbar('error message')}
            >
                error
            </Button>
        </Box>
    </>
}

export default SSEEvents;