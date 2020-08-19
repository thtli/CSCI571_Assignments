// interface/class for IP-API return

export interface Location {
    as: string;
    city: string;
    country: string;
    countryCode: string;
    isp: string;
    lat: number;
    lon: number;
    org: string;
    query: string;
    region: string;
    regionName: string;
    status: string;
    timezone: string;
    zip: string;
}