# dladm show-vnic | grep uc_

LINK             OVER    MACADDRESS       MACADDRTYPE
uc_Msrv_IFACE0   uc_Ss1  2:8:20:83:18:98  random     
uc_Mcvod_IFACE0  uc_Ss2  2:8:20:8f:a8:d0  random     
uc_Mcl_IFACE0    uc_Ss3  2:8:20:da:2a:12  random     
uc_Mch_IFACE0    uc_Ss3  2:8:20:47:72:a2  random     
uc_Rr0_IFACE1    uc_Ss2  2:8:20:31:2e:cd  random     
uc_Rr0_IFACE2    uc_Ss3  2:8:20:ed:60:99  random     
uc_Rr0_IFACE0    uc_Ss1  2:8:20:73:d1:22  random     


# flowadm show-flow | grep uc_

FLOW                   IPADDR         PROTO LPORT RPORT
uc_Msrv_IFACE0_vod     --             tcp   80    --   
uc_Msrv_IFACE0_stream0 --             udp   6970  --   
uc_Msrv_IFACE0_stream1 --             udp   6971  --   
uc_Rr0_IFACE2_low      RMT:3.3.3.3/32 --    --    --   
uc_Rr0_IFACE2_high     RMT:3.3.3.2/32 --    --    --   
