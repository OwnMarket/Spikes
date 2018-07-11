namespace Chainium.Network.Gossip

[<CLIMutable>]
type NodeAddress = {
    IPAddress : string
    Port : int
    Local : bool
}

[<CLIMutable>]
type NodeConfig = {
    InitialNodes : NodeAddress list
    TFail : int
    Cycle : int
}

[<CLIMutable>]
type Member = {
    Id : string
    IPAddress : string
    Port : int
    mutable Heartbeat : int
}

[<CLIMutable>]
type GossipDiscoveryMessage = {
    IPAddress : string
    Port : int
    ActiveMembers : Member list
}

type MulticastMessage = MulticastMessage of string
type GossipMessage = GossipMessage of string

type PeerMessage = 
    | GossipDiscoveryMessage of GossipDiscoveryMessage
    | MulticastMessage of MulticastMessage
    | GossipMessage of GossipMessage
